package com.mcxiaoke.modifier

import groovy.text.SimpleTemplateEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.text.SimpleDateFormat

/**
 * User: mcxiaoke
 * Date: 15/11/23
 * Time: 14:40
 */
class ArchiveApkTask extends DefaultTask {

    @Input
    def theVariant

    @Input
    AndroidModifierExtension theExtension

    @Input
    List<String> theMarkets

    ArchiveApkTask() {
        setDescription('modify original apk file and move to archive dir')
    }

    @TaskAction
    void showMessage() {
        project.logger.info("${name}: ${description}")
    }

    @TaskAction
    void modify() {
        def File target = theVariant.outputs[0].outputFile
        project.logger.info("${name}: modify origin apk file: ${target.absolutePath}")
        File tempDir = new File(project.rootProject.buildDir, "apkTemp")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        if (!theExtension.archiveOutput.exists()) {
            theExtension.archiveOutput.mkdirs()
        }
        for (String market : theMarkets) {
            String apkName = buildApkName(theVariant, market)
            File tempFile = new File(tempDir, apkName);
            ZipUtils.copy(target, tempFile)
            project.logger.info("${name}: modify apk file ${tempFile.absolutePath}")
            File finalFile = new File(theExtension.archiveOutput, apkName)
            ZipUtils.writeMarket(tempFile, market)
            ZipUtils.copy(tempFile, finalFile)
        }
        ZipUtils.deleteDir(tempDir)
    }

    /**
     *  build human readable apk name
     * @param variant Variant
     * @return final apk name
     */
    String buildApkName(variant, market) {
        def dateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        def buildTime = dateFormat.format(new Date())
                .replaceAll('\\.', '-')
                .replaceAll(':', '-')
                .replaceAll(' ', '-')
        def nameMap = [
                'appName'    : project.name,
                'projectName': project.rootProject.name,
                'flavorName' : market,
                'buildType'  : variant.buildType.name,
                'versionName': variant.versionName,
                'versionCode': variant.versionCode,
                'appPkg'     : variant.applicationId,
                'buildTime'  : buildTime
        ]

        def defaultTemplate = AndroidModifierExtension.DEFAULT_NAME_TEMPLATE
        def engine = new SimpleTemplateEngine()
        def template = theExtension.archiveNameFormat == null ? defaultTemplate : theExtension.archiveNameFormat
        def fileName = engine.createTemplate(template).make(nameMap).toString();
        def apkName = fileName + '.apk'
        project.logger.info("buildApkName() final apkName: " + apkName)
        return apkName
    }
}

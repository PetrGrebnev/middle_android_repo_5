import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

abstract class FindUntranslatedStringsTask : DefaultTask() {

    companion object {
        const val PATH_RES = "src/main/res"
        const val STRINGS_RES = "strings.xml"
        const val STRING_TAG = "string"
        const val VALUES_TAG = "values"
        const val NAME_TAG = "name"
    }

    @TaskAction
    fun findUntranslatedStrings() {

        val resDir = File(project.projectDir, PATH_RES)
        //получаем все файлы с ресурсами стрингов
        val stringsMapFiles = getFilesStings(resDir)
        // получаем список имен строковых ресурсов из каждого файла
        val stringsMapName = getStringsName(stringsMapFiles)
        // получаем имена не переведенных строк
        val missingStrings = getMissingStrings(stringsMapName)

        if (missingStrings.isNotEmpty()) {
            val stringBuilderErrorText =
                StringBuilder("Missing translations").append(System.lineSeparator())
            missingStrings.entries.map { missing ->
                stringBuilderErrorText
                    .append("=== ${missing.key} === ")
                    .append(System.lineSeparator())
                    .append(missing.value.joinToString(separator = System.lineSeparator()))
                    .append(System.lineSeparator())

            }
            throw GradleException(stringBuilderErrorText.toString())
        }
    }

    private fun getStringsName(files: Map<String, File>): Map<String, List<String>> {
        val result = mutableMapOf<String, List<String>>()
        files.entries.map { entry ->
            val list = getStringsFromXml(entry.value).let { nodeList ->
                (0 until nodeList.length).map { index ->
                    val node = nodeList.item(index)
                    node.attributes?.getNamedItem(NAME_TAG)?.nodeValue ?: ""
                }
            }
            result.put(entry.key, list)
        }
        return result
    }

    private fun getStringsFromXml(file: File): NodeList {
        return DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(file)
            .getElementsByTagName(STRING_TAG)
    }

    private fun getFilesStings(resDir: File): Map<String, File> {
        return resDir.listFiles().let { files ->
            val map = mutableMapOf<String, File>()
            files.map { file ->
                val fileName = file.name
                if (fileName.contains(VALUES_TAG)) {
                    map.put(fileName, File(resDir, "/$fileName/$STRINGS_RES"))
                }
            }
            map
        }
    }

    // за эталон берем список strings из values/strings.xml
    private fun getMissingStrings(stringsMap: Map<String, List<String>>): Map<String, List<String>> {
        val missingStrings = mutableMapOf<String, List<String>>()

        stringsMap.entries.map { entry ->
            if (entry.key != VALUES_TAG) {
                val missing = compareLists(stringsMap[VALUES_TAG]!!, entry.value)
                if (missing.isNotEmpty()) {
                    missingStrings.put(entry.key, missing)
                }
            }
        }
        return missingStrings
    }

    private fun compareLists(targetList: List<String>, otherList: List<String>): List<String> {
        val missingStrings = mutableListOf<String>()
        targetList.forEach { string ->
            val findString = otherList.firstOrNull() { it == string } ?: ""
            if (findString.isEmpty()) {
                missingStrings.add(string)
            }
        }
        return missingStrings
    }
}

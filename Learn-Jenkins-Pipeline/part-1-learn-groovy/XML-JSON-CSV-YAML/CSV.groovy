@Grab('com.xlson.groovycsv:groovycsv:1.3') /// загружаем библиотеку обработки csv
import static com.xlson.groovycsv.CsvParser.parseCsv

loadFile = new File('example.csv')
def csv_content = loadFile.getText('utf-8')

def dataAll = parseCsv(csv_content, separator: ',', readFirstLine: true)
for (line in dataAll) {
    println line[2] + " | " + line[3]
}
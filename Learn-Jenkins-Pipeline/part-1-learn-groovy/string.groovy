// Интерполяция строк
jstring = "Java String"
gString = "Groovy String + ${jstring}"
println(gString)

// Конкатенация строк
println("Hello World" + "!")

String str1 = "This is "
str1 = str1.concat("DEMO")
println str1

// Виды строк
def lang = 'a single-quoted string' // java.lang.String
def gString = "a double-quoted string" //groovy.lang.GString

println(gString)

def aMultilineString = '''line one

line two

line three'''

println(aMultilineString)

// Индексация
def step = 'Study Stepik'
println step[4]     // y 
println step[-1]    // k 

// Срезы строк
def stepOne = 'Study Stepik'

println stepOne[2..4]  // срез udy
println stepOne[1..<3] // срез tu
println stepOne[4..2]  // обратный срез
println stepOne[4, 1, 6] // избирательная нарезка ytS

// Операции со строками
def str = "HeLLo"
println str.size()   // Выведет длину строки
println str.length()   // Выведет длину строки

def strOne = "HeLLo"
println "string.size(): " + strOne.size()   // Выведет длину строки
println "string.length(): " + strOne.length()   // Выведет длину строки

def myList = [0, 1, 2, 3, 4];
println "myList.size(): " +  myList.size() 
// println "myList.length(): " + myList.length() // можете расскоментировать и увидите ошибку


def baseop = 'Study Stepik'
println 'Hello' + 'world!' // Helloworld!
println 'hi' * 3           // hihihi
println baseop - 'Step'   // Study ik
println "baseop.size(): " + baseop.size()         // 12
println "baseop.length(): " + baseop.length()       // 12
println "baseop.count('t') " + baseop.count('t')     //  2
println "baseop.contains('ik'): " + baseop.contains('ik')  // true


// Сравнение строк
println 'Stepik' <=> 'Stepik'  // 0
println 'A' <=> 'Stepik'       // -1
println 'stepik' <=> 'Stepik'  //  1
println 'stepik'.compareTo('Stepik') // 32
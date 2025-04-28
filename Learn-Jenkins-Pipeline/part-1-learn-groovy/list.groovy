// ----- 6 - LIST example ------
println '----------------------------------------------------------'
def exampleList = [0, 1, 2, 3, 4]
def emptyList = []

println 'list[0]: ' + exampleList[0]

// Добавление новых переменных в список
exampleList[5] = 5
exampleList << 6 << 7
exampleList += [8, 9 , 1000000]


def numbers = [0, 1, "Hello", 3, true]  
println numbers[0]  // обращение к элементу по индексу

println numbers[0..3]
  
def empty = []  // Создание пустого списка

// добавление в индекс списка
numbers[3] = 1000
// добавление в конец списка
numbers << 100
numbers << 1001
println(numbers)
println numbers += [10, 20]

def numbers1 = [1, 2, 3]
def numbers2 = [4, 5, 6]
def newnumbers = numbers1 + numbers2  //объединяет два списка и создает новый
println newnumbers  // результат: [1, 2, 3, 4, 5, 6]

println numbers1 + [10,20]

println '----------------------------------------------------------'

// Итерации по списку
[100, 2000, 30000, 400000].each {
    println "Item: $it" // it - неявный параметр, соответствующий текущему элементу
}

['Valera', 'Vovan', 'Petro', 'Dmitro'].eachWithIndex { 
    it, i -> println "$i: $it"  // it — текущий элемент,  i — индекс
}

// Перебор списка
for ( iter in exampleList) { println iter }
println '----------------------------------------------------------'

// Методы списка
numbers = [[1, 2, 3], [4, 5], [6, 7, 8, 9]]
def flattenedNumbers = numbers.flatten()
println(flattenedNumbers)

// Методы: add, addAll, contains, get, getAt, intersect, isEmpty, leftShift
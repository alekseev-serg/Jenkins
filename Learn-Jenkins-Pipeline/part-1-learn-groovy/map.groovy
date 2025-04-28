// ----- 8 - MAP example ------

def newMap = [name: 'Valera', age: '44', id: 8]
def emptyMap = [:]

newMap['name']='Vasy'
newMap.name='Egor'

emptyMap.put("newKey", 1000)
println emptyMap.get('newKey')

newMap.each { key, value -> println "$key and $value" }
println("--------------------------------------------------------------")

def m = ['first' : 100, 'second' : 500, 'last' : 999]
m.put('age', 33) // добавит ключ : значение
println m.containsKey('age') // вернет true or false в зависимости есть ли ключ
println m.values().asList() // вернет список значений[100, 500, 999, 33]
println m.keySet() // вернет список ключей [first, second, last, age]
println m.size()  // вернет размер карты 4
println m.get('second') // вернет значения ключа second, т.е 500 

println("--------------------------------------------------------------")
def map = [ Apple: 42,
            Raspberry: 54,
            Onion: 13,
            Pear: 70 ]

map.each { entry -> println "key: $entry.key | value: $entry.value" }

println("--------------------------------------------------------------")
map.eachWithIndex { 
    entry, i -> //  entry - это запись карты, i индекс на карте
    println "$i - Name: $entry.key Price: $entry.value" 
}

map.eachWithIndex { key, value, i -> // Ключ, значение и индекс 
    println "$i - Name: $key Price: $value"
}
// сделайте импорт JsonSlurper
import groovy.json.JsonSlurper 

def jsonSlurper = new JsonSlurper() // создаем объект JsonSlurper

// Затем мы используем функцию parseText класса JsonSlurper для анализа некоторого текста JSON.
def object = jsonSlurper.parseText('{ "name": "Evgeniy Lestopadov" } ')

// Когда мы получаем объект, мы можем получить доступ к значениям в строке JSON через ключ.
println object.getClass()  // class org.apache.groovy.json.internal.LazyMap
println object         // [name:Evgeniy Lestopadov]
println object.name    // Evgeniy Lestopado

// JSON OUTPUT
// сделайте импортJsonOutput
import groovy.json.JsonOutput 
def json = JsonOutput.toJson([name: 'John Doe', age: 42])

println json // {"name":"John Doe","age":42}
 
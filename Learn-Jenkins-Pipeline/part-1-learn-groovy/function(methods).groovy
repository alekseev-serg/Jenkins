// ----- 7 - Function Groovy ------

String strMethod() {
    'This is string from method'
    return 'This is ZERO 0'
}

def paramMethod(param1) {
    "Parameter: $param1"
}

println(paramMethod('Hello'))

static String strParamMethod(String arg1) {
    println "This is string: $arg1"
}

def paramDefault(String a, Integer b = 100, def c = 500) {
                    println 'Text: ' + a
                    println "Default: $b and ${c}"
                }


println 'strMethod(): ' + strMethod()
println 'paramMethod(): ' + paramMethod(2340)

paramDefault('Hello')

def add(int a, int b) {
    return a + b
}

println(add(4,5))

// Переменное число аргументов
def check(Object[] args)  { args.length }
println check() == 0 // true
println check(1) == 1 // true
println check(1, 2) == 2 // true

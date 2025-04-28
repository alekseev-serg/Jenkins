class Person {   // начало класса с именем Person
    String name  // строковое поле с именем name                     
    Integer age  // целочисленное поле с именем age

    def increaseAge(Integer years) {  // это определение метода класса Person
        this.age += years
    }
}

def p = new Person()
p.name = 'John'
println(p.name)
p.age = 10
p.increaseAge(11)
println(p.age)

class Outer {
    private String privateStr = 'outer'

    def callInnerMethod() {
        new Inner().methodA()   // создается экземпляр внутреннего класса и вызывается его метод    
    }

    class Inner {      // определение внутреннего Inner класса, внутри внешнего Outer класса        
        def methodA() {
            // будучи закрытым, доступ к полю вшешнего класса есть у внутреннего классом
            println "${privateStr}."  
        }
    }
}
def o = new Outer()
o.callInnerMethod()
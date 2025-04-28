// assert 2 + 1 == 4

def isEven = { num -> num % 2 == 0 }
assert isEven(5): "The number must be even" // Число должно быть чётным

def isString = { obj -> obj instanceof String }
def str = "Hello"
assert isString(str) // не вызовет исключения, так как str является строкой

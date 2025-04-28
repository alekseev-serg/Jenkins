// бинарные операторы
println('Бинарный операторы')
println(2 + 4)
println(7 - 2)
println(2 * 4)
println(2 / 4)
println(8 % 4)
println(2**4)

// унарные операторы
println('Унарные операторы')
println(+2 == 2)
println(-10 == 0 - 10)
println(-(-100) == 100)

// приращения
println('Инкрименты')
int x1 = 0
int x2 = 0
int y1 = 4
int y2 = 4

println(++x1)
println(x2++)
println(--y1)
println(y2--)

// Постфиксный инкримент
println('Постфиксный инкримент')
int x = 5
println(x++)   // значение x до инкремента
println(x)     // значение x после инкремента

// Префиксный инкримент
println('Префиксный инкримент')
int y = 9
println(++y)
println(y)

// Операторы присваивания
println('Присваивания')
int q = 7
println(q += 2)
println(q -= 2)
println(q *= 2)
println(q /= 2)
println(q %= 2)

// Операторы сравнения
println('Операторы сравнения')
println(2 == 2)
println(3 != 2)
println(2 < 3)
println(3 <= 3)
println(3 > 2)
println(3 >= 2)

// Логические операторы
println('Логические операторы')
println !false
println true && true
println true || false
## 🔑 Getter 和 Setter 的含义

`Getter` 和 `Setter` **不是 Java 语言的关键字**，它们是程序员们约定俗成的**方法名称前缀**，用于访问类中的私有（`private`）字段。

### 1\. Setter 方法 (设置器/赋值器)

  * **作用：** 用于给类中的私有字段**赋值**（设置值）。
  * **方法命名约定：** 通常以 `set` 开头，后跟字段名（首字母大写）。
      * *示例:* 如果字段名是 `name`，`setter` 方法名就是 `setName`。
  * **方法结构：** 接收一个参数（要设置的新值），通常没有返回值（`void`）。

<!-- end list -->

```java
public class Person {
    private String name; // 私有字段

    // Setter 方法
    public void setName(String newName) {
        // 可以在这里进行数据验证或业务逻辑
        if (newName != null && newName.length() > 0) {
            this.name = newName;
        }
    }
}
```

### 2\. Getter 方法 (获取器/访问器)

  * **作用：** 用于**获取**类中私有字段的值。
  * **方法命名约定：** 通常以 `get` 开头，后跟字段名（首字母大写）。对于布尔类型（`boolean`）字段，有时也会用 `is` 开头。
      * *示例:* 如果字段名是 `name`，`getter` 方法名就是 `getName`。
  * **方法结构：** 没有参数，返回字段对应的数据类型的值。

<!-- end list -->

```java
public class Person {
    private String name; // 私有字段

    // Getter 方法
    public String getName() {
        return this.name;
    }
}
```

-----

## 🛡️ Getter 和 Setter 的核心作用：封装

它们存在的根本目的是实现**封装 (Encapsulation)**，这是面向对象编程的三大基石之一。

### 1\. 保护数据 (Data Hiding)

  * **问题：** 如果类中的字段是 `public` 的，那么任何外部代码都可以随意修改它，导致数据的安全性和有效性无法保证。
      * *错误示例:* `person.age = -50;`
  * **解决：** 将字段声明为 `private`，外部代码无法直接访问。只能通过 `public` 的 `getter/setter` 方法间接访问。

### 2\. 控制访问和数据验证

通过 `setter` 方法，你可以在数据被设置之前加入**控制逻辑**或**验证逻辑**：

  * **验证：** 确保输入的数据是合法的（例如，年龄不能是负数、银行余额不能低于零）。
  * **权限控制：** 只有满足特定条件的外部对象才能修改字段。
  * **内部处理：** 当字段被修改时，可以同时触发其他必要的内部操作（例如，更新另一个相关字段、记录日志等）。

### 3\. 隔离内部实现 (Decoupling)

使用 `getter/setter` 可以在不改变外部调用代码的情况下，修改类的内部实现细节：

  * 你可以将一个字段从 `String` 换成 `int`，只要 `getter/setter` 方法的签名不变，外部使用该方法的代码就不需要修改。
  * `getter` 方法甚至不需要对应一个实际的字段，它可以是**计算属性**。例如，`getAge()` 可以通过当前的日期和私有的 `birthDate` 字段计算出来。

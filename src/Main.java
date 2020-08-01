import lombok.extern.slf4j.Slf4j;

/**
 * todo
 *
 * @author: 04637@163.com
 * @date: 2020/8/1
 */
public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        person.setAge("13");
        person.setName("hello world");
        System.out.println(person.getAge());
        System.out.println(person.getName());
    }
}

package test.de;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * todo
 *
 * @author: 04637@163.com
 * @date: 2020/8/4
 */
@Data
@Slf4j
public class Car {
    // Yeah! This is my car
    private String name;
    private String owner;

    public Car(String name) {
        log.info("Yeah! This is my car.");
    }
}

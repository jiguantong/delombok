package test.de;

/**
 * todo
 *
 * @author: 04637@163.com
 * @date: 2020/8/4
 */
public class Car {
    // Yeah! This is my car
    private String hello;
    private String name;
    private String owner;

    //<editor-fold desc="delombok">
    public Car() {
    }

    public String getHello() {
        return this.hello;
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setHello(final String hello) {
        this.hello = hello;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Car)) return false;
        final Car other = (Car) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$hello = this.getHello();
        final Object other$hello = other.getHello();
        if (this$hello == null ? other$hello != null : !this$hello.equals(other$hello))
            return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$owner = this.getOwner();
        final Object other$owner = other.getOwner();
        if (this$owner == null ? other$owner != null : !this$owner.equals(other$owner))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Car;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $hello = this.getHello();
        result = result * PRIME + ($hello == null ? 43 : $hello.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $owner = this.getOwner();
        result = result * PRIME + ($owner == null ? 43 : $owner.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Car(hello=" + this.getHello() + ", name=" + this.getName() + ", owner=" + this.getOwner() + ")";
    }
    //</editor-fold>
}

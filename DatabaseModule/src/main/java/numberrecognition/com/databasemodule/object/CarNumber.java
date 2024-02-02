package numberrecognition.com.databasemodule.object;

import jakarta.persistence.*;

@Entity
@Table(name = "number_db")
public class CarNumber {
    @Id
    @GeneratedValue
    @Column(name = "car_number")
    private String number;
    private String mark;
    private String color;
    private int owners;

    public String getNumber() {
        return number;
    }

    public String getMark() {
        return mark;
    }

    public String getColor() {
        return color;
    }

    public int getOwners() {
        return owners;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setOwners(int owners) {
        this.owners = owners;
    }

    public CarNumber(String number) {
        this.number = number;
    }
    public CarNumber() {
        ;
    }

}

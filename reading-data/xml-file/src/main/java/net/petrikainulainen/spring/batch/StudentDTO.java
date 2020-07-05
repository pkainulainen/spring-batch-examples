package net.petrikainulainen.spring.batch;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains the information that's read from the XML file.
 *
 * If you don't want to add public accessor methods to this class,
 * you must annotate this class with the following annotation:
 *
 * {@code @XmlAccessorType(XmlAccessType.FIELD)}
 *
 * @see  <a href="https://stackoverflow.com/a/22196362">StackOverflow answer: What's the difference between JAXB annotations put on getter versus setters versus members?</a>
 */
@XmlRootElement(name="student")
public class StudentDTO {

    private String emailAddress;
    private String name;
    private String purchasedPackage;

    public StudentDTO() {}

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getName() {
        return name;
    }

    public String getPurchasedPackage() {
        return purchasedPackage;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPurchasedPackage(String purchasedPackage) {
        this.purchasedPackage = purchasedPackage;
    }

    @Override
    public String toString() {
        return "StudentDTO{" +
                "emailAddress='" + emailAddress + '\'' +
                ", name='" + name + '\'' +
                ", purchasedPackage='" + purchasedPackage + '\'' +
                '}';
    }
}

public class SerializedCardForJSON {
    // геттеры, сеттеры и конструктор можно создать автоматически в IDE
    public SerializedCardForJSON(String name, String about) {
        this.name = name;
        this.about = about;
    }

    public SerializedCardForJSON() {}

    // ключ name стал полем типа String
    private String name;
    // ключ link стал полем типа String
    private String about;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

//    // конструктор со всеми параметрами
//    public CardForJSON(String name, String link) {
//        this.name = name;
//        this.link = link;
//    }
//
//    // конструктор без параметров
//    public CardForJSON() {
//    }
//
//    // геттер для поля name
//    public String getName() {
//        return name;
//    }
//    // сеттер для поля name
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    // геттер для поля link
//    public String getLink() {
//        return link;
//    }
//
//    // сеттер для поля link
//    public void setLink(String link) {
//        this.link = link;
//    }
}
public static void main(String[] args) {
    Main m = new Main();

    // When called like this, GETs a webpage as a String
    S obj = m.getPage("https://www.example.com");

    if(obj.eval()) {
        String page = (String) obj.unwrap();
        System.out.println("The success path");
    } else {
        System.out.println("The failure path");
    }
}

public static void main(String[] argv) {
    Main m = new Main();

    // Returns a FailureValue, wrapping 404
    S obj = m.getPage("https://www.example.com/nosuchpage");

    if(obj.eval()) {
        System.out.println("Success");
    } else {
        switch (page) {
            case FailureValue<String> fv -> System.out.println(fv.getValue());
            case FailureException<String> fe -> System.out.println(fe.getException());
            default  -> System.out.println("As currently written, not possible.");
        }
    }
}

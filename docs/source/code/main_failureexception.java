   public static void main(String[] argv) {
       Main m = new Main();

      // Returns an instance of FailureException
       S obj = m.getPage("https://www.cannotfindthisdomain.com");
   
       if(obj.eval()) {
           String page = (String) obj.unwrap();
           System.out.println("Success");
       } else {
           switch (obj) {
               case FailureValue fv -> System.out.println(fv.getValue());
               case FailureException fe -> System.out.println(fe.getException());
               default -> System.out.println("As currently written, not possible.");
           }
       }
   }

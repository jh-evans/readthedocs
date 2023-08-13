   private S rhs(String input) {
       if(FailureUtils.oneIsNull(input)) {
        	return FailureUtils.theNull(input);
        }

       if(input.indexOf("-") == -1) {
         return new FailureValue(-1);
       } else {
         return new Success(input.split("-")[1]);
       }
   }

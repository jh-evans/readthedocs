   public Success<String> getPage(String url) {
       try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
           final HttpGet httpget = new HttpGet(url);
   
           Result result = httpclient.execute(httpget, response -> {
               return new Result(response.getCode(), EntityUtils.toString(response.getEntity()));
           });
   
           if(result.status_code >= 200 && result.status_code <= 299) {
                   return new SuccessImpl<String>(result.page);
           } else {
                   return new FailureValueImpl<String>(result.status_code);
           }
       } catch(java.io.IOException ioe) {
               return new FailureExceptionImpl<String>(ioe);
       } catch(Exception e) {
               return new FailureExceptionImpl<String>(e);
       }
   }

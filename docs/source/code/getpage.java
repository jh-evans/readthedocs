   public S getPage(String url) {
       try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
           final HttpGet httpget = new HttpGet(url);
   
           Result result = httpclient.execute(httpget, response -> {
               return new Result(response.getCode(), EntityUtils.toString(response.getEntity()));
           });
   
           if(result.status_code >= 200 && result.status_code <= 299) {
                   return new Success(result.page);
           } else {
                   return new FV(result.status_code);
           }
       } catch(java.io.IOException ioe) {
               return new FExp(ioe);
       } catch(Exception e) {
               return new FExp(e);
       }
   }

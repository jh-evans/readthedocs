public String getFile(String filename) throws FileNotFoundException {
    String line;
    StringBuilder resultStringBuilder = new StringBuilder();

    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) { // file opened here
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return resultStringBuilder.toString();
}

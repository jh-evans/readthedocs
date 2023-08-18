public S getFile(Path filename) {
    if(FailureUtils.oneIsNull(filename)) {
        return FailureUtils.theNull(filename);
    }

    File file = filename.toFile();
    if(!file.exists()) {
        return FailureUtils.theFalse(file.exists());
    }

    String line;
    StringBuilder resultStringBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
        while ((line = br.readLine()) != null) {
            resultStringBuilder.append(line).append("\n");
        }
    } catch (IOException e) {
        return new FExp(e);
    }

    return new Success(resultStringBuilder.toString());
}

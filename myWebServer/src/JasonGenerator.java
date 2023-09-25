class JasonGenerator {
    
    public static String createMessage(String s1, String s2, String s3) {
        return new String("{ \"type\": \"message\", \"user\": \"" + s1 + "\"," +
                " \"room\": \"" + s2 + "\", \"message\": \"" + s3 + "\" }");
    }

    public static String createJoinMessage(String var0, String var1) {
        return new String("{ \"type\": \"join\", \"room\": \"" + var1 + "\", \"user\": \"" + var0 + "\" }");
    }

    public static String createLeaveMessage(String var0, String var1) {
        return new String("{ \"type\": \"leave\", \"room\": \"" + var1 + "\", \"user\": \"" + var0 + "\" }");
    }
}


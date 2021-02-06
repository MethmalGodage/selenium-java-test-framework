package utilities;

public class StringHandler {

    public String maskSensitiveInformation(String originalText, String maskingCharacter) {
        StringBuilder maskedText = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            maskedText.append(maskingCharacter);
        }
        return maskedText.toString();
    }

}

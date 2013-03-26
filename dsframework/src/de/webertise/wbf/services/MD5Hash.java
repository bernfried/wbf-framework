package de.webertise.wbf.services;

public class MD5Hash {

  public MD5Hash() {
  }

  public final static String getHash(String hashStr) {
    try {
      java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
      byte[] array = md.digest(hashStr.getBytes());
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < array.length; ++i) {
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
    }
    return null;
  }

}

package com.esports.gtplatform.Utilities

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64

/**
 * Created by Matthew on 8/1/2014.
 */
object ApiSecurity {
  def calculateHMAC(secret: String, applicationName: String , hostname: String ) : String  = {
    val signingKey = new SecretKeySpec(secret.getBytes(),"HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(signingKey)
    val rawHmac = mac.doFinal((applicationName + "|" + hostname).getBytes())

    new String(Base64.encode(rawHmac))
  }
  def checkHMAC(secret: String, applicationName: String, hostname: String, hmac: String) : Boolean  = {
    calculateHMAC(secret, applicationName, hostname) == hmac
  }
}

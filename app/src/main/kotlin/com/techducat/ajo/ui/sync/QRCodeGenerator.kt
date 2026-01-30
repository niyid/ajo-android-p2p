package com.techducat.ajo.ui.sync

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Complete QR Code generator for referral codes
 */
object QRCodeGenerator {
    
    fun generate(content: String, size: Int = 512): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    fun generateWithLogo(content: String, size: Int = 512): Bitmap {
        val qr = generate(content, size)
        // Could add app logo in center here
        return qr
    }
}

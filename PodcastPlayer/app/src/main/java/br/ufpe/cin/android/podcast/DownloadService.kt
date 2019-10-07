package br.ufpe.cin.android.podcast

import android.Manifest
import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class DownloadService : IntentService("DownloadService") {

    override fun onHandleIntent(intent: Intent?) {
        try {
            // Verificando se possui permissao de escrita no armazenamento
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

                //Criando instancia do DB
                val db = ItemsFeedDB.getDatabase(this@DownloadService)
                //Fazendo query para pegar itemFeed com titulo igual ao passado no intent.data
                var item = db.itemsFeedDAO().buscaItemPeloTitulo(intent!!.data!!.toString())
                //Salvando o downloadLink do itemFeed
                val itemDownloadLink = Uri.parse(item.downloadLink)

                Log.e("Teste", "DOWNLOAD INICIADO")
                Log.e("Teste", itemDownloadLink.toString())

                //Fazendo o download do podcast
                val root = getExternalFilesDir(DIRECTORY_DOWNLOADS)
                root?.mkdirs()
                val output = File(root, itemDownloadLink.lastPathSegment)
                if (output.exists()) {
                    output.delete()
                }
                val url = URL(itemDownloadLink.toString())
                val c = url.openConnection() as HttpURLConnection
                val fos = FileOutputStream(output.path)
                val out = BufferedOutputStream(fos)
                try {
                    val `in` = c.inputStream
                    val buffer = ByteArray(8192)
                    var len = `in`.read(buffer)
                    while (len >= 0) {
                        out.write(buffer, 0, len)
                        len = `in`.read(buffer)
                    }
                    out.flush()

                    Log.e("Finalizado", "DOWNLOAD FINALIZADO")
                    Log.e("Finalizado", output.path)

                    //Salvando localização do arquivo no DB
                    item.location = output.path
                    db.itemsFeedDAO().atualizarItem(item)

                    //Enviando broadcast informando o termino do download
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(DOWNLOAD_COMPLETE))
                } finally {
                    fos.fd.sync()
                    out.close()
                    c.disconnect()
                }
            } else {
                Log.e("Permissao", "Sem permissao de escrita no storage")
            }
        } catch (e2: IOException) {
            Log.e(javaClass.getName(), "Exception durante download", e2)
        }
    }

    companion object {
        val DOWNLOAD_COMPLETE = "br.ufpe.cin.if710.services.action.DOWNLOAD_COMPLETE"
    }
}

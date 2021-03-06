/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

// Importamos las librerias
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

/**
 *
 * @author Paulo Andrade
 */
public class Main
{   
    // Cargamos las librerias dinamicas de OpenCV
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    
    private static ScheduledExecutorService timer;
    private static VideoCapture cam;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // creamos la matriz para los frame
        Mat frame = new Mat();
        // accedemos al dispositivo
        cam = new VideoCapture();
        // configuramos el ancho y alto
        cam.set(Videoio.CAP_PROP_FRAME_WIDTH, 640);
        cam.set(Videoio.CAP_PROP_FRAME_HEIGHT, 480);
        // Creamos la ventana
        ImageViewer view = new ImageViewer("Calculo de histogramas");
        // actualizamos la configuracion
        view.set(640, 480);
        
        // Abrimos la camara
        cam.open(0);
        
        // Verificamos que accedemos al dispositivo
        if(cam.isOpened()){
            // Creamos un hilo para trabajar, tomando un frame cada 30 segundos
            Runnable frameGrabber = new Runnable() {
                @Override
                public void run() {
                    // Capturamos el frame
                    cam.read(frame);

                    // Detectamos los rostros
                    view.show(frame);
                }
            };
            
            timer = Executors.newSingleThreadScheduledExecutor();
            timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);    
        } else {
            stopCamera();
            // Mostramos error de carga
            System.out.println("Error al inicializar el dispositivo");
        }
    }
    
    private static void stopCamera(){
        // verificamos si el tiempo esta corriendo
        if (timer!=null && !timer.isShutdown()){
            try{
                // De tenemos el tiempo
                timer.shutdown();
                timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
	}
		
	if (cam.isOpened()){
            // release the camera
            cam.release();
	}
    }
}

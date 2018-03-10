/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

/**
 *
 * @author Paulo Andrade
 */
public class FaceDetector
{
    private final CascadeClassifier classifiersCascade; // Clasificadores 
    private final CascadeClassifier classifiersLBP;
    private final String[] cascadePath = new String[] {
        "resources/haarcascades/haarcascade_frontalface_alt.xml"
    };
    private final String[] LBPPath = new String[] {
        
    };
    int faceSize; // Tamaño del rostro a detectar
    
    /**
     * Constructor
     */
    public FaceDetector()
    {
        classifiersCascade = new CascadeClassifier();
        classifiersLBP = new CascadeClassifier();
        faceSize = 0;
        
        // Cargamos los clasificadores
        loadClassifiers();
    }
    
    /**
     * Cargamos los clasificadores con los que vamos a trabajar
     */
    private void loadClassifiers()
    {
        // cargamos los clasificadores cascada
        for(String cascade : cascadePath){
            classifiersCascade.load(cascade);
        }
        
        // cargamos los clasificadores LBP
        for(String lbp : LBPPath){
            classifiersLBP.load(lbp);
        }
    }
    
    /**
     * Detecta los rostros
     * 
     * @param m Matriz donde reconoceremos los rostros
     */
    public Mat FaceDetect(Mat m)
    {
        // Vector para los objetos detectados
        MatOfRect faces = new MatOfRect();
        // Matriz en escala de grises
        Mat grayFrame = new Mat();
        
        // Convertimos el frame original en escala de grises
        Imgproc.cvtColor(m, grayFrame, Imgproc.COLOR_BGR2GRAY);
        
        // Ecualizamos el histograma del frame para mejorar el resultado
        Imgproc.equalizeHist(grayFrame, grayFrame);
        
        // Calculamos el tamaño minimo del rostro a detectar (20%)
        if (this.faceSize == 0){
            // Obtenemos el alto de la imagen (frame)
            int height = grayFrame.rows();
            
            // verificamos que el minimo a detectar sea mayor a 0
            if (Math.round(height * 0.2f) > 0){
                // Asignamos el nuevo tamaño a detectar
                this.faceSize = Math.round(height * 0.2f);
            }
        }
        
        // detectamos los rostros (imagen donde detectaremos los objetos,
        // vector de cuadrados para los objetos detectados,
        // cuanto se reduce la imagen en cada escala de imagen,
        // cuantos vecinos debe tener cada rectangulo candidato para concervarlo,
        // flags,
        // tamaño minimo posible del objeto
        // tamaño maximo posible del objeto
        classifiersCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.faceSize, this.faceSize), new Size());
        
        return faceDraw(m, faces);
    }
    
    /**
     * Dibujamos rectangulos al detectar los rostros
     * 
     * @param m Matriz original
     * @param faces Matriz con los rectangulos detectados
     */
    private Mat faceDraw(Mat m, MatOfRect faces)
    {
        // Convertimos la matriz en un vector
        Rect[] facesArray = faces.toArray();
        
        // Recorremos cada uno de los objetos
	for (int i = 0; i < facesArray.length; i++){
            // m <- matriz original
            // facesArray[i].tl() <- punto del objeto en x
            // facesArray[i].br() <- punto del objeto en y
            // new Scalar(0, 255, 0) <- color del rectangulo a dibujar
            // 3 <- Espesor de la linea
            Imgproc.rectangle(m, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 1);
        }
                
        return m;
    }
}
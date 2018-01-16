package pfe.polytech.Vuzix_M100_entrepot;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Valentin BISSUEL on 14/01/2018.
 */

public class CompassView extends View {
    // ATTRIBUTS
    private float northOrientation = 0;

    private Paint   circlePaint,
                    leftTriangleSide,
                    rightTriangleSide;
    private Path    leftTrianglePath,
                    rightTrianglePath;

    // CONSTRUCTEUR
    public CompassView(Context context) {
        super(context);
        initView();
    }

    // Constructeur utilisé pour instancier la vue depuis sa
    // déclaration dans un fichier XML

    /**
     * Constructeur utilisé pour instancier la vue depuis sa
     * déclaration dans un fichier XML.
     * @param context voir définition de View
     * @param attrs voir définition de View
     */
    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    /**
     * idem que précedemment
     * @param context voir définition de View
     * @param attrs voir définition de View
     * @param defStyle voir définition de View
     */
    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    // GETTERS ET SETTERS
    /*
     * permet de récupérer l'orientation de la boussole
     * @return Retourne l'orientation de la boussole
     *//*
    public float getNorthOrientation() {
        return northOrientation;
    }*/
    /**
     * permet de changer l'orientation de la boussole
     * @param rotation angle en degrée
     */
    public void setNorthOrientation(float rotation) {
        if (rotation != this.northOrientation) {    // on met à jour l'orientation uniquement si elle a changé
            this.northOrientation = rotation;
            this.invalidate();                      // on demande à notre vue de se redessiner
        }
    }
    // METHODS

    /*
     * Permet de définir la taille de notre vue
     * @param widthMeasureSpec largeur du parent de notre vue
     * @param heightMeasureSpec hauteur du parent de notre vue
     *//*
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth  = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredWidth, measuredHeight); // Notre vue sera un carré, on garde donc le minimum
        setMeasuredDimension(d, d);
    }*/

    /*
     * Détermine la taille de notre vue
     * @param measureSpec le parent de notre vue
     * @return Retourne la taille de notre vue
     *//*
    private int measure(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            // Le parent ne nous a pas donné d'indications,
            // on fixe donc une taille par défaut
            result = 150;
        } else {
            // On va prendre la taille de la vue parente
            result = specSize;
        }
        return result;
    }*/

    private void initView(){
        Resources r = this.getResources();

        // Paint pour l'arrière plan de la boussole
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Lisser les formes
        circlePaint.setColor(r.getColor(R.color.compassCircle)); // Définir la couleur

        // Paint pour les 2 aiguilles, Nord et Sud
        leftTriangleSide = new Paint(Paint.ANTI_ALIAS_FLAG);
        leftTriangleSide.setColor(r.getColor(R.color.leftTriangleSidePointer));
        rightTriangleSide = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightTriangleSide.setColor(r.getColor(R.color.rightTriangleSidePointer));

        // Path pour dessiner les aiguilles
        leftTrianglePath = new Path();
        rightTrianglePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas){
        int centerX = getMeasuredWidth() / 2;        // Centre de notre point de vue
        int centerY = getMeasuredHeight() / 2;
        int radius = Math.min(centerX, centerY);    // Diametre du cercle
        // Dessin du cercle avec le pinceau circlePaint
        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        canvas.save();
        // Rotation du canevas pour que le nord pointe vers le haut
        canvas.rotate(-northOrientation, centerX, centerY);
        // Def du tracé du triangle coté Gauche + dessin
        leftTrianglePath.reset();
        leftTrianglePath.moveTo((float)(centerX + radius*0.8*Math.cos(2.0*Math.PI/3.0)) + 0,
                                (float)(centerY + radius*0.8*Math.sin(2.0*Math.PI/3.0)) + 0);
        leftTrianglePath.lineTo(centerX, (float)(centerY - radius*0.8));
        leftTrianglePath.lineTo(centerX, (float)(centerY + radius*0.5));
        //canvas.rotate(180, centerX, centerY);
        canvas.drawPath(leftTrianglePath, leftTriangleSide);
        // Def du tracé du triangle coté Droit + dessin
        rightTrianglePath.reset();
        rightTrianglePath.moveTo((float)(centerX + radius*0.8*Math.cos(Math.PI/3.0)) + 0,
                                 (float)(centerY + radius*0.8*Math.sin(Math.PI/3.0)) + 0);
        rightTrianglePath.lineTo(centerX, (float)(centerY - radius*0.8));
        rightTrianglePath.lineTo(centerX, (float)(centerY + radius*0.5));
        canvas.drawPath(rightTrianglePath, rightTriangleSide);
        // Restauration position initiale
        canvas.restore();
    }
}

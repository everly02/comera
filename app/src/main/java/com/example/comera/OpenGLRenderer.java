package com.example.comera;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "OpenGLRenderer";
    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 vPosition;" +
                    "attribute vec2 vTexCoord;" +
                    "varying vec2 aTexCoord;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "  aTexCoord = vTexCoord;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
                    "varying vec2 aTexCoord;" +
                    "uniform sampler2D uTexture;" +
                    "uniform int uFilter;" +
                    "void main() {" +
                    "  vec4 color = texture2D(uTexture, aTexCoord);" +
                    "  if (uFilter == 1) {" +
                    "    float gray = (color.r + color.g + color.b) / 3.0;" +
                    "    gl_FragColor = vec4(gray, gray, gray, 1.0);" +
                    "  } else if (uFilter == 2) {" +
                    "    gl_FragColor = vec4(color.r, 0.0, 0.0, 1.0);" +
                    "  } else if (uFilter == 3) {" +
                    "    gl_FragColor = vec4(0.0, 0.0, color.b, 1.0);" +
                    "  } else {" +
                    "    gl_FragColor = color;" +
                    "  }" +
                    "}";

    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private int program;
    private int textureId;
    private int filterType = 0;

    private static final float[] VERTEX_COORDS = {
            -1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f,  1.0f, 0.0f
    };

    private static final float[] TEX_COORDS = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    private static final short[] DRAW_ORDER = { 0, 1, 2, 0, 2, 3 };

    public OpenGLRenderer() {
        vertexBuffer = ByteBuffer.allocateDirect(VERTEX_COORDS.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(VERTEX_COORDS).position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(TEX_COORDS.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoordBuffer.put(TEX_COORDS).position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        textureId = createTexture();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        int texCoordHandle = GLES20.glGetAttribLocation(program, "vTexCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");
        GLES20.glUniform1i(textureHandle, 0);

        int filterHandle = GLES20.glGetUniformLocation(program, "uFilter");
        GLES20.glUniform1i(filterHandle, filterType);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private int createTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        return textures[0];
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }
}

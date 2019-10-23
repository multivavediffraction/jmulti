package org.structureviewer;

import javajs.util.P3;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
    @NotNull
    public static String readResource(String fileName)
    {
        InputStream input = StructureViewer.class.getResourceAsStream(fileName);
        if(null == input){
            throw new AssertionError("Can't find resource: " + fileName);
        }
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(input)))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while (true)
            {
                line = br.readLine();
                if (line == null)
                {
                    break;
                }
                sb.append(line).append("\n");
            }
            br.close();
            return sb.toString();
        }
        catch (IOException e)
        {
            throw new AssertionError("Can't read resource: " + fileName);
        }
    }

    public static float[] matMul(float[] a, float[] b, int n){
        float[] result = new float[n*n];

        for(int i = 0; i<n; ++i){
            for(int j = 0; j<n; ++j){
                float r = 0.0f;
                for(int k = 0; k<n; ++k){
                    r += a[i*n + k]*b[k*n + j];
                }
                result[i*n + j] = r;
            }
        }

        return result;
    }

    public static float[] reMat(float[] a, int n){
        float[] result = new float[n*n];

        for(int i = 0; i<n; ++i){
            for(int j = 0; j<n; ++j){
                result[i*n + j] = a[j*n + i];
            }
        }

        return result;
    }

    public static boolean epsilonEquals(P3 p1, P3 p2, float epsilon) {
        P3 diff = new P3();
        diff.sub2(p1,p2);
        return -epsilon < diff.x && diff.x < epsilon
                && -epsilon < diff.y && diff.y < epsilon
                && -epsilon < diff.z && diff.z < epsilon;
    }

    public static P3 toJmolP3(org.jmulti.calc.P3 v) {
        return P3.new3((float)v.x(), (float)v.y(), (float)v.z());
    }
}

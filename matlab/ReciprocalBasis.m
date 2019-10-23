function [A, B, C] = ReciprocalBasis (a, b, c)
    V = det( [a; b; c]);
    A = 2 * pi * cross(b,c)/V;
    B = 2 * pi * cross(c,a)/V;
    C = 2 * pi * cross(a,b)/V;
end
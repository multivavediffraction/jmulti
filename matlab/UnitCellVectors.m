function [A, B, C] = UnitCellVectors(a, b, c, alpha, beta, gamma)
    rad = pi / 180;
    A = [a 0 0];
    B = [b * cos(gamma*rad)  b * sin(gamma*rad) 0];

    cz = (c/sin(gamma*rad)) * sqrt( ...
                1 - cos(alpha*rad)^2 - cos(beta*rad)^2 - cos(gamma*rad)^2  ...
                + 2*cos(alpha*rad)*cos(beta*rad)*cos(gamma*rad) ...
        );
    C = [c * cos(beta*rad)
         c * (cos(alpha * rad) - cos(beta*rad)*cos(gamma*rad))/sin(gamma*rad) 
         cz]';
end
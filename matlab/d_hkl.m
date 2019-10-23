function d = d_hkl (a, b, c, alpha, beta, gamma, h, k, l)
        rad = pi/180;
        sin_a = sin(alpha * rad);
        cos_a = cos(alpha * rad);
        sin_b = sin(beta * rad);
        cos_b = cos(beta * rad);
        sin_c = sin(gamma * rad);
        cos_c = cos(gamma * rad);
        
        d = 1/ sqrt( ((h*h*sin_a*sin_a/(a*a)) + (k*k*sin_b*sin_b/(b*b)) + (l*l*sin_c*sin_c/(c*c)) + ...
             ((2*k*l*cos_a)/(b*c)) + ((2*h*l*cos_b)/(a*c)) + ((2*h*k*cos_c)/(a*b))) / ...
             (1 - cos_a*cos_a - cos_b*cos_b - cos_c*cos_c + 2*cos_a*cos_b*cos_c));
end
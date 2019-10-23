function a = angle(v0, v1, n)
    v0 = v0/norm(v0);
    v1 = v1/norm(v1);
    
    a = atan2(norm(cross(v0,v1)), dot(v0, v1));
    if dot(n, cross(v0, v1)) < 0
        a = 2*pi - a;
    end
end
function A = intersection(a1, a2, n, d)
    ray = a2 - a1;
    ray = ray/norm(ray);
    a = dot(d*n - a1, n);
    b = dot(ray,n);
    if b == 0
        A = 0;
    else
        A = (a/b) *  ray + a1;
    end
end
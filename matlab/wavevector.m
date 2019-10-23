a = 4.914000;
b = 4.914000;
c = 5.406000;
alpha = 90;
beta = 90;
gamma = 120;

phi = 0;
theta = 30;

h = 1; k = 1; l = 1;

epsilon = 1e-5;

[A1, A2, A3] = UnitCellVectors(a, b, c, alpha, beta, gamma);
[B1, B2, B3] = ReciprocalBasis(A1, A2, A3);

normalize = @(x) x/norm(x); 

%% Calculating plane data for visualisation

p = [0 0 0; A1; A2; A3; A1+A2; A1+A3; A2+A3; A1+A2+A3];
box = [0 0 0; A1; A1+A2; A2; 0 0 0; A2; A2+A3; A3; 0 0 0; A3; A3+A1; A1;...
    A1+A2; A1+A2+A3; A1+A3; A1+A2+A3; A2+A3];

N = h*B1 + k*B2 + l*B3;
N = normalize(N);
d = d_hkl(a, b, c, alpha, beta, gamma, h, k ,l);

R = [0 0 0 A1; 0 0 0 A2; 0 0 0 A3; A1 A1+A2; A1 A1+A3; A2 A2+A1; A2 A2+A3; ...
     A3 A3+A1; A3 A3+A2; A1+A2 A1+A2+A3; A1+A3 A1+A2+A3; A2+A3 A1+A2+A3];

% P - intersection points of plane and unit cell
P = [];
%P = zeros(size(R,1), 3);

for i = 1:size(R,1)
    p1 = R(i,1:3);
    p2 = R(i,4:6);
    x = intersection(p1, p2, N, d);
    if ~isscalar(x)
        r = dot(x - p1, p2 - p1)/(norm(p2 - p1)^2);
        if 0-epsilon <= r && r <= 1 + epsilon
            P = [P;x];
        end
    end    
end

% C - center between intersection points
if size(P,1) ~= 0
    C = [sum(P(:,1)) sum(P(:,2)) sum(P(:,3))] / size(P,1);
else
    C = [0 0 0];
end

% sorting intersection points clockwise for plotting purposes
angles = zeros(size(P,1), 1);
for i = 1:size(P,1)
    angles(i) = angle(P(1,:)-C, P(i,:)-C, N);
end

P = [angles P];
P = sortrows(P);

%% Calculating wavevector from phi and theta
% A0 - reference vector for phi

if abs(dot(A1,N)) > abs(dot(A2,N)) && abs(dot(A1,N)) > abs(dot(A3,N)) % plane more perpendicular to A1 basis vector
    A0 = normalize(A2);
    B0 = normalize(A3);
elseif abs(dot(A2,N)) > abs(dot(A1,N)) && abs(dot(A2,N)) > abs(dot(A3,N)) % plane more perpendicular to A2 basis vector
    A0 = normalize(A3);
    B0 = normalize(A1);
else % plane more perpendicular to A3 basis vector
    A0 = normalize(A1);
    B0 = normalize(A2);
end

T = [C; A0 + C];

NN = normalize(cross(A0,B0));

if 1e-10 < norm(cross(N,NN))
    A0 = normalize(cross(N,NN));
end

B0 = cross(N,A0);

V = [C; A0 + C];
VV = [C; B0 + C];

%% Plotting data

hold on;
 
plot3(p(:,1), p(:,2), p(:,3), 'ob');
plot3(box(:,1), box(:,2), box(:,3), '-k');
plot3(V(:,1), V(:,2), V(:,3), '-r');
plot3(T(:,1), T(:,2), T(:,3), '-m', 'LineWidth', 2);
plot3(VV(:,1), VV(:,2), VV(:,3), '-g');
patch(P(:,2), P(:,3), P(:,4), 0);
axis equal;
 
hold off;

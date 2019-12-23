file = '../Rl2-field-SiO2-ortho-(002)-E 5.0 6.0 500-psi 0.0 120.0 500 par.csv';
values = dlmread(file, ';', 1, 1);
Psi = dlmread(file, ';', 0, 1);
Psi = Psi(1,:);
energy = dlmread(file, ';', 1, 0);
energy = energy(:,1);

map = [linspace(0,.5,127)' linspace(0,.5,127)' linspace(1,.5,127)'
       0.5 0.5 0.5
       linspace(.5,1,127)' linspace(.5,0,127)' linspace(.5,0,127)'];

mesh(Psi, energy', log(values));   
%GR = mat2gray(log(values));   
%[I, graymap] = gray2ind(GR,256);   
%imshow(I, graymap);
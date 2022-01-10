%[num,txt,raw] = xlsread('CuB2O4-002-Rr2-field-java.xlsx');
%[numL,txtL,rawL] = xlsread('Rl2-field-C4 H6 O6 no H.cif-(005)-E 3.0 5.0 200-psi 0.0 180.0 9000 par.csv');
%[numR,txtR,rawR] = xlsread('Rr2-field-C4 H6 O6 no H.cif-(005)-E 3.0 5.0 200-psi 0.0 180.0 9000 par.csv');

fileL = '../Rl2-field-SiO2-Stishovite.cif-(001)-E 4.0 5.0 100-psi 0.0 120.0 1200 par.csv';
fileR = '../Rr2-field-SiO2-Stishovite.cif-(001)-E 4.0 5.0 100-psi 0.0 120.0 1200 par.csv';

RL = dlmread(fileL, ';', 1, 1);
psiL = dlmread(fileL, ';', [0 1 0 size(RL,2)]);
energyL = dlmread(fileL, ';', [1 0 size(RL,1), 0]);

[PsiL, EL] = meshgrid(psiL, energyL);

RR = dlmread(fileR, ';', 1, 1);
psiR = dlmread(fileR, ';', [0 1 0 size(RR,2)]);
energyR = dlmread(fileR, ';', [1 0 size(RR,1), 0]);

[PsiR, ER] = meshgrid(psiL, energyL);

mesh(PsiR, ER, log(RL));

%mesh(Psi, E, R, 'EdgeColor', 'none', 'FaceLighting', 'gouraud');
%set(gca, 'ZScale', 'log')
%axis square;

map = [linspace(0,.5,127)' linspace(0,.5,127)' linspace(1,.5,127)'
       0.5 0.5 0.5
       linspace(.5,1,127)' linspace(.5,0,127)' linspace(.5,0,127)'];
%[I, graymap] = gray2ind(mat2gray(log(R)),255);
%lRR = log(RR);
%lRL = log(RL);
%GR = mat2gray(lRR); % requires image processing toolbox
%GL = mat2gray(lRL); % requires image processing toolbox
%DIFF = mat2gray(lRR-lRL); % requires image processing toolbox
%imshow(gray2ind(DIFF), map); % requires image processing toolbox
%imwrite(GR, 'quartz-right-polarization.png'); % requires image processing toolbox
%imwrite(GL, 'quartz-left-polarization.png'); % requires image processing toolbox
%[I, graymap] = gray2ind(GR,255);
%imshow(I, graymap);
%image(psiL, energyL, log(RR)-log(RL), 'CDataMapping','scaled') %  plots matrix as 2d image, no special reqirements
%colorbar
colormap(map)
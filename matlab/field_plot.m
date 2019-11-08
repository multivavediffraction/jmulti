%[num,txt,raw] = xlsread('CuB2O4-002-Rr2-field-java.xlsx');
[numL,txtL,rawL] = xlsread('SiO2-002-Rl2-field-java.xlsx');
[numR,txtR,rawR] = xlsread('SiO2-002-Rr2-field-java5.xlsx');

psiL = numL(1, 2:end)';
energyL = numL(2:end, 1);

[PsiL, EL] = meshgrid(psiL, energyL);
RL = numL(2:end, 2:end);

psiR = numR(1, 2:end)';
energyR = numR(2:end, 1);

[PsiR, ER] = meshgrid(psiR, energyR);
RR = numR(2:end, 2:end);


%mesh(Psi, E, R);
%mesh(Psi, E, R, 'EdgeColor', 'none', 'FaceLighting', 'gouraud');
%set(gca, 'ZScale', 'log')
%axis square;

map = [linspace(0,.5,127)' linspace(0,.5,127)' linspace(1,.5,127)'
       0.5 0.5 0.5
       linspace(.5,1,127)' linspace(.5,0,127)' linspace(.5,0,127)'];
%[I, graymap] = gray2ind(mat2gray(log(R)),255);
lRR = log(RR);
lRL = log(RL);
GR = mat2gray(lRR); % requires image processing toolbox
GL = mat2gray(lRL); % requires image processing toolbox
DIFF = mat2gray(lRR-lRL); % requires image processing toolbox
imshow(gray2ind(DIFF), map); % requires image processing toolbox
%imwrite(GR, 'quartz-right-polarization.png'); % requires image processing toolbox
%imwrite(GL, 'quartz-left-polarization.png'); % requires image processing toolbox
%[I, graymap] = gray2ind(GR,255);
%imshow(I, graymap);
%image(psiL, energyL, log(RR)-log(RL), 'CDataMapping','scaled') %  plots matrix as 2d image, no special reqirements
%colorbar
%colormap(map)
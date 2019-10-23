[num1,txt1,raw1] = xlsread('SiO2-002-orthorhombic-fortran.xlsx');
[num2,txt2,raw2] = xlsread('SiO2-002-orthorhombic-java.xlsx');
[num3,txt3,raw3] = xlsread('SiO2-002-hexagonal-java.xlsx');
[num4,txt4,raw4] = xlsread('SiO2-002-hexagonal-2-java.xlsx');
[tt4,ttt4,tttt4] = xlsread('tt.xlsx');

%t = [num4(:,1)+180 num4(:,2)];
t = sortrows([mod(num4(:,1)+180,360) num4(:,2)]);

hold on;
%semilogy(num4(:,1), num4(:,2), '-r');
%semilogy(num4(:,1), num4(:,3), '-b');
%semilogy(t(:,1), t(:,2), '-r');
%semilogy(num1(:,1), num1(:,2), '-b');
%semilogy(num1(:,1), num1(:,3), '-r');
%semilogy(num1(:,1), num1(:,2), '-k');
semilogy(tt4(:,1), tt4(:,2), '-b');
set(gca, 'YScale', 'log')
hold off;
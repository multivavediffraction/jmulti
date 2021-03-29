@default_files = ('coordinate_system.tex');

$pdf_mode = 5;
$dvi_mode = 0;
$postscript_mode = 0;

$pdf_previewer="emacsclient -e '(find-file-other-window %S)'";
$xelatex='xelatex %O -interaction=nonstopmode -synctex=1 %S';
$pdf_update_method = 4;
$pdf_update_command = "emacsclient -e '(with-current-buffer (find-buffer-visiting %S) (pdf-view-revert-buffer nil t))'";
@generated_exts = (@generated_exts, 'synctex.gz');


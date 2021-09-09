/**
 * @license Copyright (c) 2003-2017, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 * 참고 : http://nightly.ckeditor.com/17-12-20-15-11/full/samples/toolbarconfigurator/index.html#basic
 */
CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here. For example:
	config.height = "400px";//editor 높이
	config.font_names = "Conv_CPINYINC;Conv_CPINYIN;SimHei;SimSun;msyh;msyhbd;SeoulNamsan;" + config.font_names;//editor 글꼴
	config.contentsCss = [["/css/editorFont/cpinyinc.css"]
						, ["/css/editorFont/cpinyin.css"]
						, ["/css/editorFont/SimHei.css"]
						, ["/css/editorFont/SimSun.css"]
						, ["/css/editorFont/msyh.css"]
						, ["/css/editorFont/msyhbd.css"]
						, ["/css/editorFont/SeoulNamsan.css"]];//폰트 설정
	config.fontSize_defaultLabel = "1em";//editor 클씨 크기
	config.line_height = "0.3em";//줄 간격
	config.fontSize_sizes = "0.5em; 0.5625em; 0.625em; 0.6875em; 0.75em; 0.8125em; 0.875em; 0.9375em; 1em; 1.0625em; 1.125em; 1.1875em; 1.25em; 1.375em; 1.5em; 1.625em; 1.75em; 2.25em; 3em; 4.5em;";
	config.enterMode = CKEDITOR.ENTER_BR;
	
	/* toolbar 설정 */	//,Source 삭제후 배포
	config.toolbarGroups = [
		{ name: 'styles', groups: [ 'styles' ] },
		{ name: 'colors', groups: [ 'colors' ] },
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'clipboard', groups: [ 'clipboard', 'undo' ] },
		{ name: 'editing', groups: [ 'find', 'selection', 'spellchecker', 'editing' ] },
		{ name: 'forms', groups: [ 'forms' ] },
		{ name: 'paragraph', groups: [ 'list', 'indent', 'blocks', 'align', 'bidi', 'paragraph' ] },
		{ name: 'links', groups: [ 'links' ] },
		{ name: 'insert', groups: [ 'insert' ] },
		{ name: 'tools', groups: [ 'tools' ] },
		{ name: 'others', groups: [ 'others' ] },
		{ name: 'about', groups: [ 'about' ] },
		{ name: 'document', groups: [ 'mode', 'document', 'doctools' ] }
	];

	config.removeButtons = 'About,ShowBlocks,Format,Styles,Iframe,PageBreak,SpecialChar,Smiley,Table,Flash,Anchor,Unlink,Link,BidiLtr,BidiRtl,Language,Blockquote,CreateDiv,Indent,Outdent,NumberedList,BulletedList,RemoveFormat,CopyFormatting,Subscript,Strike,Save,NewPage,Preview,Print,PasteFromWord,PasteText,Paste,Copy,Cut,Undo,Redo,Replace,Find,SelectAll,Scayt,Form,Checkbox,Radio,TextField,Textarea,Select,Button,ImageButton,HiddenField,Maximize,Templates,Image';
};
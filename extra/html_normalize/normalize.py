#!/usr/bin/env python
# -*- coding: utf-8 -*-
#--------------------------------------------
# Author: Petrus Augusto
# Date: 06-07-2014
# Corp: ESPE
#--------------------------------------------
import sys, os, codecs, glob

major_version = sys.version_info.major
if major_version == 2:
    import htmlentity2ascii
else:
    import html
#/if,else

def unescape(str):
    if major_version == 2:
        return htmlentity2ascii.convert(str)
    else:
        return html.unescape(str)
#/def

# ---------------------------------------------------------------------------- #
# FUNCOES DO SCRIPT
# ---------------------------------------------------------------------------- #
def workWithFile(filename):
    if '.html' not in filename.lower():
        print('Error: vocé deve especificar um arquivo HTML')
        print('Finalizando script')
        return 1
    
    src_fname = filename
    final_fname = src_fname + ".newhtml"

    # Abrindo/Criando arquivos
    #fd0 = codecs.open(src_fname, 'rb', 'utf-8')
    fd0 = open(src_fname, 'rb')
    fd1 = codecs.open(final_fname, 'wb', 'utf-8')

    # Lendo cada linha do arquivo e aplicando algoritimo de convers�o/remo��o
    # dos c�digos esp�ciais do html (html_entity)
    lineCount = 0
    for rdline in fd0:
        lineCount += 1
        try: # Decodifincando UTF
            rdline = rdline.decode('utf-8')
        except: # Erro... Decodifincando ANSU
            # Checando char por char para modificar o caracter
            rdline = rdline.decode('cp1252', 'ignore')
        
        # Continuando a tratar o arquivo
        rdline = rdline.replace('&#160;', ' ')
        new_line = unescape(rdline)
        new_line = new_line.replace('&#160;', ' ')
		
		# Petrus -> 09-11-2017
		# 	Checando se contem as tags ('<br><br/></br>'),
		# 	se sim, ira criar uma nova linha com os dados ja cortados mas
		#	mantendo a definicao da tag original
        if '<br>' in new_line or '<br/>' in new_line or '</br>' in new_line:
            # Garantindo que a tag e correta para o 'split'
            new_text = new_line.replace("<br>", "<br/>")
            new_text = new_text.replace("</br>", "<br/>")
            splitted_text = new_text.split("<br/>")

            # Obtendo a tag de abertura
            try:
                charPos = splitted_text[0].index(">")
                tagOpenText = splitted_text[0][:(charPos + 1)]

                # Obtendo a tag de fechamento
                last_line = splitted_text[(len(splitted_text) - 1)]
                charPos = last_line.rfind("<")
            except:
                # Ignorando esta linha...
                continue
            
            tagCloseText = last_line[charPos:]

            # Percorrendo a array do 'split' e definindo as novas linhas
            lenArray = len(splitted_text)
            for idx in range(lenArray):
                newLineText = ""
                # Checando se deve colocar a tag inicial
                if (idx + 1) > 1:
                    newLineText += tagOpenText

                # Anexando o texto
                newLineText += splitted_text[idx]

                # Checando se deve colocar a tag final
                if (idx + 1) < lenArray:
                    newLineText += tagCloseText

                # Escrevendo no arquivo
                fd1.write(newLineText)
            #/for
        else:
            # Escrevendo no arquivo
            fd1.write(new_line)

    # Fechando arquivos abertos para remocao e renomeacao do novo arquivo
    # devidamente convertido/trabalhado
    fd0.close()
    fd1.close()

    # Removendo arquivo HTML antigo, e renomeando o novo
    os.remove(src_fname)
    os.rename(final_fname, src_fname)
    
    return 0 # Sucesso
# /def workWithFile(filename):

def workWithFolder(folder):
    for filename in os.listdir(folder):
        if filename == '.' or filename == '..':
            continue
        elif '.html' not in filename.lower():
            continue
        
        # Processando este arquivo
        workWithFile( folder + '/' + filename )
    # /for
    
    return 0
# /def workWithFolder(folder):


# ---------------------------------------------------------------------------- #
# EXECUCAO PRINCIPAL DO SCRIPT (MAIN)
# ---------------------------------------------------------------------------- #
# Validando argumento...
if len(sys.argv) != 2: # Nota: o 1o (argv[0]) e argumento e o nome do script
    print('Error: Especifique o nome do arquivo HTML a ser convertido')
    print('Finalizando script')
    exit(1)

# Checando se o argumento recebido aponta para um arquivo ou dirtetorio
if os.path.isdir(sys.argv[1]): # Trabalhando com os arquivos dentro do diretorio
    exit( workWithFolder(sys.argv[1]) )
else: # Trabalhando com o arquivo diretamente
    exit ( workWithFile(sys.argv[1]) )

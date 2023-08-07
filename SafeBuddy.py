from flask import Flask, render_template, request, redirect
from twilio.rest import Client
import time
import threading

app = Flask(__name__)

# Lista de contatos de confiança
contatos_confianca = []

# Adicione suas credenciais da API Twilio aqui
account_sid = "your_account_sid"
auth_token = "your_auth_token"
twilio_phone_number = "your_twilio_phone_number"

client = Client(account_sid, auth_token)

# Função para enviar mensagem via WhatsApp
def enviar_mensagem_whatsapp(contatos):
    for contato in contatos:
        client.messages.create(
            body="Esta é uma mensagem automática. Tudo bem?",
            from_=f"whatsapp:{twilio_phone_number}",
            to=f"whatsapp:{contato['telefone']}",
        )

# Função para gerar notificações a cada 10 minutos
def gerar_notificacoes():
    while True:
        time.sleep(600)  # Aguarda 10 minutos (600 segundos)
        print("Está tudo bem?")

# Função para ligar para a emergência
def ligar_para_emergencia():
    print("Ligando para emergência...")

# Rota principal do aplicativo
@app.route("/", methods=["GET", "POST"])
def index():
    if request.method == "POST":
        nome = request.form["nome"]
        telefone = request.form["telefone"]
        contatos_confianca.append({"nome": nome, "telefone": telefone})
        return redirect("/")
    return render_template("index.html", contatos=contatos_confianca)

# Inicie o aplicativo e as notificações
if __name__ == "__main__":
    notificacoes_thread = threading.Thread(target=gerar_notificacoes)
    notificacoes_thread.start()
    app.run(debug=True)

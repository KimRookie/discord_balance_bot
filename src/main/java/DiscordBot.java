import javax.security.auth.login.LoginException;

import events.InteractionEventListener;
import events.MessageEventListener;
import events.ReadyEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class DiscordBot {
	
	public static void main(String[] args) throws LoginException {
		String BOT_TOKEN = System.getenv("BOT_TOKEN");
		JDABuilder jdaBuilder = JDABuilder.createDefault(BOT_TOKEN);
		JDA jda = jdaBuilder
			.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
			.addEventListeners(new ReadyEventListener(), new MessageEventListener(), new InteractionEventListener())
			.build();
		
		jda.upsertCommand("설명서", "내전 봇 사용법 안내").setGuildOnly(true).queue();
		
	}
	
}

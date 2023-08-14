package events;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InteractionEventListener extends ListenerAdapter{

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		// TODO Auto-generated method stub
		super.onSlashCommandInteraction(event);
		System.out.println("Interaction!");
		try {
			switch (event.getName()) {
			case "설명서": 
				event.reply( "**해당 봇은 포지션을 고려하지 않고, 티어 순으로 팀을 만듭니다.**\n"
						+ "■ 기능요약 ■\n"
						+ "■ !밸런스: 팀 밸런싱 실행 / !수정: 멤버 정보 수정 ■\n"
						+ "■ 고정기능 활용 예시: 탑만하는 유저 둘이 같은 팀 되는 상황 방지, 라이벌 구도일 때 같은 팀 방지 ■\n"
						+ "1. **!밸런스** 라고 입력하여 기능을 실행합니다.\n"
						+ "2. 참여자들이 본인을 **@아이디 티어** 형식으로 입력하면 한 명씩 등록됩니다.(혼자 여러 개 등록도 됩니다)\n"
						+ "2.1 아이디와 티어 사이는 공백(스페이스바)으로 구분합니다. **아이디에 공백이 있으면 붙여서** 써주세요.\n"
						+ "2.2 티어는 대충써도 반영됩니다만, 마스터 이상부터는 뒤에 꼭 점수를 붙여주세요.\n"
						+ "*(예. 가능: 에1, 애1, 에매1, 마스터200, 마200, 그마400)*\n"
						+ "*(예. 불가능: 플 1, 그마, 챌린저, 마스터 300)*\n"
						+ "3. 팀 결과는 티어 순으로 배정됩니다. 내전 진행자가 포지션을 고려하여 결과물에서 일부 팀원을 바꿔서 진행하셔도 좋습니다.\n"
						+ "4. **!수정**은 멤버 정보(아이디or티어)를 잘못 입력했거나, 멤버가 바뀌었을 때 사용하시는 기능입니다.").setEphemeral(true).queue();
				break;
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("에러발생. 개발자한테 연락주세요 010 6808 9174");
		}
	}

}

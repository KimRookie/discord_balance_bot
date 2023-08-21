package events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class MessageEventListener extends ListenerAdapter{

	private static final String prefix = "!";

    private final List<String> names = new ArrayList<>();
	String tier;
	List<String> NameAndTier = new ArrayList<>();
    List<Integer> scores = new ArrayList<>();
    String[] changeName;
	int[] exchange = new int[2];
	ArrayList<Integer> modifyChange = new ArrayList<>();
    private int currentPlayer = 1;
    private String buttonId;
    List<String> namesList = new ArrayList<>();
    String garbage;
    String fixed = "off";
    private boolean modify = false;
    private boolean allowBotMessages = false; // 봇 메세지 허용 여부, false: 불가 / true: 허용
    private boolean end = false;
    
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		// TODO Auto-generated method stub
		super.onMessageReceived(event);
		
		Message message = event.getMessage();
		if (!allowBotMessages && message.getAuthor().isBot()) return;

	    String[] args = message.getContentRaw().split(" ");
	    String command = args[0].toLowerCase();

	    try {
	    if (command.equals(prefix + "밸런스")) {
	        names.clear();
	        scores.clear();
	        NameAndTier.clear();
	        modifyChange.clear();
	        namesList.clear();
	        changeName = null;
	        currentPlayer = 1;
	        modify = false;
	        garbage ="";
	        // !밸런스를 입력하면 onMessageReceived가 재시작 되야하는데, 실제론 if부터 다시 시작하기 때문에 위 변수들을 초기화해줘야 정상적으로 진행이 가능
	        
	        message.getChannel().sendMessage(currentPlayer + "번째 참여자의 닉네임과 티어를 입력해주세요" +" (예. @Player1 플2)").queue();
	    } else if (command.matches("^@[\\p{L}\\p{N}]+$") && args.length == 2) {
	            tier = args[1];
	            names.add(command.substring(1, command.length()));
	            scores.add(change(tier));
	            NameAndTier.add(message.getContentDisplay().substring(1));
	            System.out.println(NameAndTier);
	            currentPlayer++;
	            if (currentPlayer <= 10) {
	                message.getChannel().sendMessage(currentPlayer + "번째 참여자를 입력하세요").queue();
	            } else {
	            	if(modify) {
	            		modifyChange.clear(); // 수정 기능을 두 번 이상 이용할 시, 기존 수정 멤버 번호가 남아있는 현상 방지
	            		for (int i = 0; i < changeName.length; i++) {
	    	    			modifyChange.add(Integer.parseInt(changeName[i])-1);
	    	    		}
	            		Collections.sort(modifyChange, Collections.reverseOrder());
	            		System.out.println("modifyChange: " + modifyChange);
	            		for (int j = 0; j < modifyChange.size(); j++) {
							int i = modifyChange.get(j);
							System.out.println(i);
							names.remove(i);
							scores.remove(i);
							NameAndTier.remove(i);
            				System.out.println("names: " + names);
            				System.out.println("scores: " + scores);
						}
	            	}
	            	message.getChannel().sendMessage("각 팀에 고정시킬 두 명을 정하시겠습니까?")
	            	.setActionRow(
	            			Button.success("fix", "정한다"),
	            			Button.primary("non-fix", "안정한다")
	            			)
	            	.queue();
	            }
	    }
	    // 라인 고정여부에 따른 처리 (+ 멤버 수정을 위한 작업이 겹쳐서 포함)
	    if (command.equals("!fix") || command.equals("!수정")) {
	    	allowBotMessages = false;
	    	namesList.clear();
	    	for (int i = 0; i < names.size(); i++) {
	    		namesList.add((i+1) + ". " + NameAndTier.get(i));
	    	}
	    	String nameListMessage = String.join("\n", namesList);
	    	event.getChannel().sendMessage(nameListMessage).queue();
	    	if (command.equals("!fix")) {
	    		event.getChannel().sendMessage("고정할 두 명의 번호를 입력하세요 (스페이스바로 구분, 예: 4 7): ").queue();
			} else if(command.equals("!수정")) {
				event.getChannel().sendMessage("빠지는 멤버의 번호를 입력하세요 (스페이스바로 구분, 예: 4 7 1): ").queue();
				end = false; // 수정 전 첫 밸런싱때 end값이 true가 되어 팀 목록 다시 출력되는 것 방지
			}
	    	garbage = message.getContentDisplay();
	    } else if (command.equals(prefix + "non-fix")) {
	    	allowBotMessages = false;
	    	event.getChannel().sendMessage("라인고정 없이 밸런스를 맞춥니다.").queue();
	    	fixed="off";
	    	end = true;
	    } else if(garbage.equals("!fix")) {	// garbage로 !fix 메시지가 남아있는것 처리용도로 받은 다음, args로 고정한 번호 입력받은 것 intArray로 전환하여 swap
	    		for (int i = 0; i < 2; i++) {
	    			exchange[i] = Integer.parseInt(args[i])-1;
	    			Collections.swap(names, i, exchange[i]);
	    			Collections.swap(scores, i, exchange[i]);
	    			Collections.swap(NameAndTier, i, exchange[i]);
	    		}
	    		fixed = "on";
	    		garbage = "";
	    		end = true;
    	} else if(garbage.equals("!수정")) { // 멤버 수정 로직
	    	changeName = args;
	    	currentPlayer -= args.length;
	    	message.getChannel().sendMessage(currentPlayer + "번째 참여자를 입력하세요").queue();
    		modify = true;
    		end = false;
    		garbage="";
	    }
	    
	    // 최종 팀배정 로직
	    if(end) {
	    	end = false;
	    	List<List<String>> teams = balanceTeams(names, scores, fixed);
	    	MessageEmbed embed = createEmbed(teams.get(0), teams.get(1));
	    	System.out.println(NameAndTier);
	    	message.getChannel().sendMessageEmbeds(embed).queue();
	    }
	    } catch (NumberFormatException e) {
	    	message.getChannel().sendMessage("A-Type에러발생").queue();
	    	e.printStackTrace();
	    } catch (Exception e) {
	    	message.getChannel().sendMessage("B-Type 에러발생").queue();
	    	e.printStackTrace();	    	
		}
	}

    private static int change(String tier) {
    	int score = 0;
		
		String cut = tier.substring(0, 1);
		char number = tier.charAt(tier.length()-1);
		
		if ("아".equals(cut)) {
			tier="아이언";
		} else if ("브".equals(cut)) {
			tier="브론즈";
		} else if ("실".equals(cut)) {
			tier="실버" + number;
		} else if ("골".equals(cut)) {
			tier="골드" + number;
		} else if ("플".equals(cut)) {
			tier="플레티넘" + number;
		} else if ("에".equals(cut) || "애".equals(cut)) {
			tier="에메랄드" + number;
		} else if ("다".equals(cut)) {
			tier="다이아" + number;
		} else if ("마".equals(cut) || "그".equals(cut) || "챌".equals(cut)) {
			try {
				char masterCut = tier.charAt(tier.length()-3);
				if(masterCut == '마' || masterCut == '스' || masterCut == '터') {
					number = '0';
				} else {
					number = masterCut;
				}
			} catch (Exception e) {
				// TODO: handle exception
				if(tier.charAt(tier.length()-1) == '마') {
					number = '0';
				}
			}
			tier="마/그/챌" + number;
		}
		switch (tier) { //2023 아프리카tv 멸망전 티어 점수표 반영
		case "아이언":
			score = 5;
			break;
		case "브론즈":
			score = 5;
			break;
		case "실버4", "실", "실버":
			score = 9;
			break;
		case "실버3":
			score = 11;
			break;
		case "실버2":
			score = 13;
			break;
		case "실버1":
			score = 14;
			break;
		case "골드4", "골드", "골":
			score = 16;
			break;
		case "골드3":
			score = 17;
			break;
		case "골드2":
			score = 18;
			break;
		case "골드1":
			score = 19;
			break;
		case "플레티넘4", "플레", "플":
			score = 21;
			break;
		case "플레티넘3":
			score = 22;
			break;
		case "플레티넘2":
			score = 23;
			break;
		case "플레티넘1":
			score = 24;
			break;
		case "에메랄드4", "에메랄드", "에메", "에":
			score = 26;
			break;
		case "에메랄드3":
			score = 27;
			break;
		case "에메랄드2":
			score = 28;
			break;
		case "에메랄드1":
			score = 29;
			break;
		case "다이아4", "다이아", "다":
			score = 31;
			break;
		case "다이아3":
			score = 33;
			break;
		case "다이아2":
			score = 35;
			break;
		case "다이아1":
			score = 39;
			break;
		case "마/그/챌0":
			score = 43;
			break;
		case "마/그/챌1":
			score = 45;
			break;
		case "마/그/챌2":
			score = 46;
			break;
		case "마/그/챌3":
			score = 48;
			break;
		case "마/그/챌4":
			score = 50;
			break;
		case "마/그/챌5":
			score = 51;
			break;
		case "마/그/챌6":
			score = 52;
			break;
		case "마/그/챌7":
			score = 53;
			break;
		case "마/그/챌8":
			score = 54;
			break;
		case "마/그/챌9":
			score = 56;
			break;
		default:
			score = 5;
			break;
		}
		return score;
	}

    
	@Override
	public void onButtonInteraction(ButtonInteractionEvent event) {
		// TODO Auto-generated method stub
		super.onButtonInteraction(event);
		buttonId = event.getComponentId();
		allowBotMessages = true; // 봇의 메시지도 받을 수 있게 바꿈
		if (!allowBotMessages && event.getMessage().getAuthor().isBot()) return;
        event.reply(prefix + buttonId).queue(); // 봇이 메시지를 보내고, 그걸 봇이 받아서 라인 고정여부 처리
	}

	private List<List<String>> balanceTeams(List<String> names, List<Integer> scores, String fixed) {
        List<List<String>> teams = new ArrayList<>();
        List<String> team1 = new ArrayList<>();
        List<String> team2 = new ArrayList<>();
        int onoff = 0;
        System.out.println("names:" + names + ", scores: " + scores);
        // 팀에 고정시킨 사람들 할당
        if(fixed=="on") {
        	team1.add(names.get(0));
        	team2.add(names.get(1));
        	onoff = 2;
        } else if (fixed=="off") {
        	onoff = 0;
        }
        
        // 이름과 티어별 점수 정렬
        for (int i = onoff; i < names.size() - 1; i++) {
            for (int j = i + 1; j < names.size(); j++) {
                if (scores.get(i) < scores.get(j)) {
                    Collections.swap(names, i, j);
                    Collections.swap(scores, i, j);
                    Collections.swap(NameAndTier, i, j);
                }
            }
        }
        
        for (int i = 9; i >= onoff; i--) {
            if (i % 4 == 1 || i % 4 == 2) {
                team2.add(names.get(i));
            } else {
                team1.add(names.get(i));
            }
        }
        teams.add(team1);
        teams.add(team2);
        return teams;
    }

    private MessageEmbed createEmbed(List<String> team1, List<String> team2) {
        StringBuilder team1Builder = new StringBuilder();
        StringBuilder team2Builder = new StringBuilder();
        for (String id : team1) {
            team1Builder.append("<").append(id).append(">\n");
        }
        for (String id : team2) {
            team2Builder.append("<").append(id).append(">\n");
        }
        return new EmbedBuilder()
                .addField("Team 1", team1Builder.toString(), true)
                .addField("Team 2", team2Builder.toString(), true)
                .build();
	}
}

package org.giglab.live.simulation;

import java.util.List;
import org.giglab.live.application.dto.action.Actor;

public class SimulationScenario {

  public static final List<Actor> VIEWERS =
      List.of(
          new Actor("sim_user_01", "viewer01@test.com", "민지"),
          new Actor("sim_user_02", "viewer02@test.com", "현우"),
          new Actor("sim_user_03", "viewer03@test.com", "소연"),
          new Actor("sim_user_04", "viewer04@test.com", "태양"),
          new Actor("sim_user_05", "viewer05@test.com", "하은"),
          new Actor("sim_user_06", "viewer06@test.com", "준서"),
          new Actor("sim_user_07", "viewer07@test.com", "나연"),
          new Actor("sim_user_08", "viewer08@test.com", "도현"),
          new Actor("sim_user_09", "viewer09@test.com", "서아"),
          new Actor("sim_user_10", "viewer10@test.com", "지훈"));

  // 저장된 시뮬레이션 메시지가 없을 때 사용하는 폴백 데이터
  public static final List<String> FALLBACK_CHAT_MESSAGES =
      List.of(
          "이거 진짜 효과 있나요?",
          "가격이 어떻게 돼요?",
          "피부 트러블 있어도 써도 되나요?",
          "배송 얼마나 걸려요?",
          "저도 써봤는데 진짜 좋더라구요!",
          "할인 언제까지예요?",
          "민감성 피부인데 괜찮을까요?",
          "향이 강한가요?",
          "유통기한이 어떻게 돼요?",
          "이거 진짜 사야겠다!",
          "냉장 보관해야 하나요?",
          "다른 제품이랑 같이 써도 되나요?",
          "재구매 했어요 진짜 최고",
          "선물 포장 가능한가요?",
          "임산부도 사용 가능한가요?",
          "해외배송도 되나요?",
          "몇 번이나 사용할 수 있어요?",
          "후기 믿을 수 있나요?",
          "색깔이 실제로도 저렇게 예쁜가요?",
          "지금 재고 있나요?");

  public static final List<String> FALLBACK_FAQ_QUESTIONS =
      List.of(
          "이 제품의 주요 성분이 무엇인가요?",
          "민감성 피부에도 사용 가능한가요?",
          "하루에 몇 번 사용하는 것이 좋나요?",
          "유통기한이 어떻게 되나요?",
          "다른 스킨케어 제품과 함께 사용해도 되나요?");

  private SimulationScenario() {}
}

// Merritt, Jason, Jerry, Coleman
import sexp._

sealed abstract class Exp
case class Call(lhs : Exp, rhs : List[Exp]) extends Exp
case class Let(v : String, rhs : Exp, body : Exp) extends Exp
case class Ref(v : String) extends Exp
case class If(condition : Exp, tCase : Exp, fCase : Exp) extends Exp
case class Equal(lhs: Exp, rhs: Exp) extends Exp
case class Def(name : String, args : List[String], body: Exp)
case class Program(defs : List[Def], exp : Exp)
case class Lambda(id: List[String], body: Exp) extends Exp
case class Quote(exp: SExp) extends Exp

case class Closure(v : Lambda, e : Env) extends SExp
case class Primitive(name: String) extends SExp

class Box[A](var contents : Option[A] = None)

type Env = Map[String, Box[SExp]]

val initialEnv: Env = Map(
	"+" -> new Box(Some(Primitive("+"))),
	"-" -> new Box(Some(Primitive("-"))),
	"*" -> new Box(Some(Primitive("*"))),
	"null?" -> new Box(Some(Primitive("null?"))),
	"cons" -> new Box(Some(Primitive("cons"))),
	"car" -> new Box(Some(Primitive("car"))),
	"cdr" -> new Box(Some(Primitive("cdr"))),
	"pair?" -> new Box(Some(Primitive("pair?"))),
	"null" ->  new Box(Some(SNil))
)

def parseProgram(p : SExp) : Program = {
	def parseProgHelp(p2 : SExp, acc : List[Def]) : Program = {
		p2 match {
			case SList(exp) => Program(acc.reverse, parseExp(exp))
			case SCons(first, rest) => parseProgHelp(rest, parseDefine(first) :: acc)
		}
	}
	parseProgHelp(p, Nil)
}

def parseDefine(p : SExp) : Def = {
	p match {
		case SList(SSymbol("define"), SCons(SSymbol(first), rest), body) => Def(first, parseDefineHelp(rest, Nil), parseExp(body))
	}
}

def parseDefineHelp(s : SExp, acc : List[String]) : List[String] = {
	s match {
		case SNil => acc.reverse
		case SCons(SSymbol(first), rest) => parseDefineHelp(rest, first :: acc)
	}
}

def parseExp(e: SExp) : Exp = {
	e match {
		case SInt(v) => Quote(SInt(v))
		case STrue() => Quote(STrue())
		case SFalse() => Quote(SFalse())
		case SList(SSymbol("let"), SList(SList(SSymbol(id), rhs)), body) => Let(id, parseExp(rhs), parseExp(body))
		case SList(SSymbol("if"), condition, tCase, fCase) => If(parseExp(condition), parseExp(tCase), parseExp(fCase))
		case SList(SSymbol("equal?"), l, r) => Equal(parseExp(l), parseExp(r))
		case SList(SSymbol("quote"), exp) => Quote(exp)
		case SSymbol(id) => Ref(id)
		case SList(SSymbol("lambda"), id, body) => Lambda(parseDefineHelp(id, List()), parseExp(body))
		case SCons(id, rest) => Call(parseExp(id), SList.toList(rest).get.map(parseExp _))
		case _ => throw new IllegalArgumentException("not a valid expression " + e)
	}
}

def interpExp(e : Exp, env : Env) : SExp = {
	e match {
		case Ref(id) => env.get(id) match {
			case None => throw new RuntimeException("unbound variable")
			case Some(v) => v.contents.get
		}
		case Let(id, rhs, body) => {
			val box = new Box[SExp]()
			box.contents =  Option(interpExp(rhs, env))
			val newEnv = env + (id -> box)
			interpExp(body, newEnv)
		}
		case Call(e1, eargs) => {
			val v1 = interpExp(e1, env)
			val vargs = eargs.map((e : Exp) => interpExp(e, env))
			v1 match {
				case Closure(Lambda(id, body), aenv) => {
					interpExp(body, interpCallHelp(id, vargs, aenv))
				}
				case Primitive("+") => {
					val SInt(arg0) = vargs(0)
					val SInt(arg1) = vargs(1)
					SInt(arg0 + arg1)
				}
				case Primitive("-") => {
					val SInt(arg0) = vargs(0)
					val SInt(arg1) = vargs(1)
					SInt(arg0 - arg1)
				}
				case Primitive("*") => {
					val SInt(arg0) = vargs(0)
					val SInt(arg1) = vargs(1)
					SInt(arg0 * arg1)
				}
				case Primitive("null?") => {
					if (vargs(0) == SNil) 
						STrue() 
					else SFalse()
				}
				case Primitive("cons") => {
					SCons(vargs(0), vargs(1))
				}
				case Primitive("cdr") => {
					vargs(0) match {
						case SCons(first, second) => second
						case _ => throw new RuntimeException("cdr error, not a pair"  + e)
					}
				}
				case Primitive("car") => {
					vargs(0) match {
					  case SCons(first, second) => first
					  case _ => throw new RuntimeException("car error, not a pair"  + e)
					}
				}
				case Primitive("pair?") => {
					vargs(0) match {
						case SCons(first, rest) => STrue()
						case _ => SFalse()
					}
				}
				case _ => throw new RuntimeException( "tried to call a non-function"  + e)
			}
		}
		case If(condition, tCase, fCase) => {
			val tf = interpExp(condition, env)
			tf match {
				case SFalse() => interpExp(fCase, env)
				case _ => interpExp(tCase, env)
			}
		}
		case Equal(l, r) => {
			val e1 = interpExp(l, env)
			val e2 = interpExp(r, env)
			if(e1 == e2) 
			  STrue()
			else
			  SFalse()
		}
		case Quote(v) => v
		case Lambda(id, body) => Closure(Lambda(id, body), env)
	}
}

def interpCallHelp(l : List[String], e : List[SExp], finalEnv : Env) : Env = {
	(l, e) match {
		case (Nil, Nil) => finalEnv
		case (lFirst :: lRest, eFirst :: eRest) => {
			val box = new Box[SExp]()
			box.contents = Option(eFirst)
			interpCallHelp(lRest, eRest, (finalEnv + (lFirst -> box)))
		}
		case _ => throw new RuntimeException("variable not referenced"  + e)
	}
}

def interpProgram(p : Program) : SExp = {
	val Program(defs, exp) = p
	val env =  interpProgramHelp(defs, initialEnv)
	defs.foreach((defs: Def) => closureHelp(defs, env))
	interpExp(exp, env)
}

def interpProgramHelp(l : List[Def], acc : Map[String, Box[SExp]]) : Map[String, Box[SExp]] = {
	l match {
		case Nil => acc
		case first :: rest => first match {
			case Def(name, args, exp) => interpProgramHelp(rest, acc + (name -> new Box[SExp]()))
		}	
	}
}

def closureHelp(defs : Def, env : Map[String, Box[SExp]]) : Unit = {
	defs match{
		case Def(name, args, body) => env(name).contents = Some(Closure(Lambda(args, body), env))
	}
}

def evalProgram(p : String) : SExp = {
	interpProgram(parseProgram(parseSExp(p)))
}